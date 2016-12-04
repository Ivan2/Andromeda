package com.games.andromeda.logic;

public class Pocket extends GameObject {
    public class NotEnoughMoneyException extends Exception {
        @Override
        public String getMessage() {
            return "У вас недостаточно денег";
        }
    }

    private int total;

    public Pocket(Side side) {
        super(side);
        total = 0;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
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
