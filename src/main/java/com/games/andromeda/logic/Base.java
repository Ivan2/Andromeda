package com.games.andromeda.logic;

import com.games.andromeda.graph.Node;

public class Base extends GameObject {
    public class IncorrectNodeException extends Exception {}
    public static class NoFriendlyFleetException extends Exception {}
    private int nodeID;
    private BaseProperties properties;

    /**
     * Базы сами по себе не появляются, поэтому конструктор deprecated.
     * Для создания базы следует использовать buy
     * @param side
     * @param node
     */
    @Deprecated
    public Base(Side side, int nodeID) throws IncorrectNodeException{
        super(side);
        switch (side){
            case EMPIRE:
                properties = new BasicEmpireBaseProperties();
                break;
            case FEDERATION:
                properties = new BasicFederationBaseProperties();
                break;
        }
        this.nodeID = nodeID;
        if (false){
            // TODO: проверка на корректность вершины
            throw new IncorrectNodeException();
        }
    }

    /**
     * Основание базы
     * @param fleet база основывается там, где есть флот
     * @param pocket база основывается за деньги
     * @return база
     * @throws IncorrectNodeException если система не пуста
     * @throws NoFriendlyFleetException если флот - не дружественный
     * @throws Pocket.NotEnoughMoneyException если не хватает денег
     */
    public static Base buy(Fleet fleet, Pocket pocket) throws IncorrectNodeException,
            NoFriendlyFleetException, Pocket.NotEnoughMoneyException {
        if (fleet.getSide() != pocket.getSide()){
            throw new NoFriendlyFleetException();
        }
        Base result = new Base(pocket.getSide(), fleet.getPosition());
        pocket.decrease(result.properties.getCost());
        return result;
    }

    public int getNodeID(){
        return nodeID;
    }

    public int getProfit(){
        return properties.getProfit();
    }
}
