package com.games.andromeda.logic;

public class Pocket extends GameObject {
    public class NotEnoughMoneyException extends Exception{}

    private int total;
    public Pocket(Side side) {
        super(side);
        total = 0;
    }

    public void increase(int sum){
        total += sum;
    }

    public void decrease(int sum) throws NotEnoughMoneyException{
        if (total < sum)
            throw new NotEnoughMoneyException();
        total -= sum;
    }
}
