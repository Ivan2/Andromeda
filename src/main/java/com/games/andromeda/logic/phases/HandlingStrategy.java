package com.games.andromeda.logic.phases;

import com.games.andromeda.logic.GameObject;

public interface HandlingStrategy<TParam, TOut> {
    void startPhase(GameObject.Side side); // обнуляет состояние
    TOut handlePhaseEvent(TParam input) throws Exception; // обрабатывает действия игрока во время фазы
    boolean applyChanges(); // вызывать, если игрок завершает фазу. true - фаза успешно завершена, false - ход невозможен
    void autoApplyChanges(); // вызывать, если у игрока вышло время и надо завершить ход автоматически
    GameObject.Side getSide();
}
