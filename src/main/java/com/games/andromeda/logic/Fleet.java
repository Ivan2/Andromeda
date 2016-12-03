package com.games.andromeda.logic;

import android.util.Log;

import com.games.andromeda.draw.Drawer;
import com.games.andromeda.graph.PathInfo;

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
    private int nodeID;
    private Integer prevNodeId;
    private static float CURRENCY = 0.0000001f;

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getEnergy() {
        return energy;
    }

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
        for (int i = 0; i < shipCount; ++i){
            ships.add(new SpaceShip());
        }
        if (base.getSide() != side){
            throw new InvalidPositionException();
        }
        this.nodeID = base.getNodeID();
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
        for(int i = 0; i < damage; ++i){
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
                result = !this.splitDamage(another.properties.getAttack(another.getShipCount()));
            }
            drawer.drawFleets(this, another);
            this.tryToRestoreShields();
            another.tryToRestoreShields();
            drawer.reDrawFleets(this, another);
        }
        return result;
    }


    public boolean attack(Fleet another){
        Boolean result = null;
        while (result == null){
            if (another.splitDamage(this.properties.getAttack(this.getShipCount()))) {
                result = true;
                if (this.splitDamage(another.properties.getAttack(another.getShipCount()))){
                    result = false;
                }
            }
            //drawer.drawFleets(this, another);
            this.tryToRestoreShields();
            another.tryToRestoreShields();
            //drawer.reDrawFleets(this, another);
        }
        return result;
    }

    public int getCost(){
        return properties.getEmptyFleetCost() + properties.getShipCost()*ships.size();
    }

    public int getPosition(){
        return nodeID;
    }

    public Integer getPrevPosition(){
        return prevNodeId;
    }

    /**
     * Выполняется перемещение флота, если оно возможно
     * @param path результат пользовательского ввода
     */
    public void makeMove(PathInfo path) throws InvalidPathException,
            InvalidPositionException, NotEnoughEnergyException {
        List<Integer> nodes = path.getNodeIds();
        if (nodes.size() < 2){
            return;
        }
        if (!nodes.get(0).equals(nodeID)){
            throw new InvalidPositionException();
        }
        float requiredEnergy =(float) (path.getLength())/properties.getSpeed(ships.size()) - CURRENCY;
        if (requiredEnergy > energy){
            throw new NotEnoughEnergyException();
        }
        Log.wtf("path", path.toString());
        Log.wtf("required_energy", String.valueOf(requiredEnergy));
        Log.wtf("energy", String.valueOf(energy));
        energy -= requiredEnergy;
        nodeID = nodes.get(nodes.size() - 1);
        prevNodeId = nodes.get(nodes.size() - 2);
    }

    @Deprecated
    public void setPosition(int nodeID){
        this.nodeID = nodeID;
    }

    public void destroyShips(int amount)
    {
        for (int i = 0; i < amount; i++)
            ships.remove(ships.size() - 1);
    }

}
