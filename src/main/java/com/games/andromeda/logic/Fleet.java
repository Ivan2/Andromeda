package com.games.andromeda.logic;

import com.games.andromeda.draw.Drawer;
import com.games.andromeda.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fleet extends GameObject {

    public class InvalidPositionException extends Exception{}
    public class InvalidPathException extends Exception{}
    public class NotEnoughEnergyException extends Exception {}
    public class TooMuchShipsException extends Exception {}

    private FleetProperties properties;
    private List<SpaceShip> ships;
    private static Random random = new Random();
    private float energy;  // 0..1
    private Node position;

    /**
     * Флоты сами по себе не появляются, поэтому конструктор deprecated.
     * Для создания флота следует использовать buy
     * @param side
     * @param shipCount
     * @param base
     */
    @Deprecated
    public Fleet(Side side, int shipCount, Base base) throws InvalidPositionException,
            TooMuchShipsException{
        super(side);
        switch (side){
            case EMPIRE:
                properties = new BasicEmpireFleetProperties();
                break;
            case FEDERATION:
                properties = new BasicFederationFleetProperties();
                break;
        }
        if (shipCount > properties.getMaxShipCount()){
            throw new TooMuchShipsException();
        }
        energy = 1;
//        ships = Stream.generate(SpaceShip::new)
//                .limit(shipCount).collect(Collectors.toList());
        ships = new ArrayList<>();
        for (int i=0; i<shipCount; ++i){
            ships.add(new SpaceShip());
        }
        if (base.getSide() != side){
            throw new InvalidPositionException();
        }
        this.position = base.getNode();
    }

    public static Fleet buy(int shipCount, Base base, Pocket pocket)
            throws Pocket.NotEnoughMoneyException, InvalidPositionException, TooMuchShipsException{
        // todo проверять количество флотов игрока. возможно, не здесь
        Fleet result = new Fleet(pocket.getSide(), shipCount, base);
        pocket.decrease(result.getCost());
        return result;
    }

    public void buyShips(int shipCount, Pocket pocket) throws TooMuchShipsException,
            Pocket.NotEnoughMoneyException{
        if (shipCount + ships.size() > properties.getMaxShipCount()){
            throw new TooMuchShipsException();
        }
        pocket.decrease(properties.getShipCost() * shipCount);
        for(int i=0; i<shipCount; ++i){
            ships.add(new SpaceShip());
        }
    }

    /**
     * Восстановление щитов после боя
     */
    private void restoreShields(){
        for(SpaceShip x: ships){
            x.restoreShield();
        }
    }

    /**
     * Восстановление щитов меджу залпами
     */
    private void tryToRestoreShields(){
        for(SpaceShip x: ships){
            x.tryToRestoreShield(properties.getShieldRestoringChance());
        }
    }

    /**
     * @return Число кораблей
     */
    public int getShipCount(){
        return ships.size();
    }

    /**
     * Нанесение урона кораблю
     * @param ship корабль
     */
    private void hitShip(SpaceShip ship){
        if (ship.hit()){
            ships.remove(ship);
        }
    }

    /**
     * Нанесение урона случайным кораблям флота
     * @param damage показатель урона
     * @return true, если флот уничтожен
     */
    private boolean splitDamage(int damage){
        for(int i=0; i<damage; ++i){
            int size = getShipCount();
            if (size == 0) return true;
            hitShip(ships.get(random.nextInt(size)));
        }
        return false;
    }

    /**
     * Атака на другой флот
     * @param another атакуемый флот
     * @param drawer интерфейс, который отрисует бой
     * @return true, если атака успешна
     */
    public boolean attack(Fleet another, Drawer drawer){
        Boolean result = null;
        while (result == null){
            if (another.splitDamage(this.properties.getAttack(this.getShipCount()))) {
                result = true;
                if (this.splitDamage(another.properties.getAttack(another.getShipCount()))){
                    result = false;
                }
            }
            drawer.drawFleets(this, another);
            this.tryToRestoreShields();
            another.tryToRestoreShields();
            drawer.reDrawFleets(this, another);
        }
        return result;
    }


    public int getCost(){
        return properties.getEmptyFleetCost() + properties.getShipCost()*ships.size();
    }

    public Node getPosition(){
        return position;
    }

    /**
     * Выполняется перемещение флота, если оно возможно
     * @param path результат пользовательского ввода
     */
    public void makeMove(List<Node> path) throws InvalidPathException, InvalidPositionException,
            NotEnoughEnergyException {
        if (false) {
            // todo проверка валидности последовательности вершин. лучше в пакете graph ?
            throw new InvalidPathException();
        }

        if (!path.get(0).equals(position)){
            throw new InvalidPositionException();
        }
        if (path.size() == 0){
            return;
        }
        float requiredEnergy = properties.getSpeed(ships.size())*energy / path.size();
        if (requiredEnergy < energy){
            throw new NotEnoughEnergyException();
        }
        energy -= requiredEnergy;
        position = path.get(path.size()-1);
    }


}
