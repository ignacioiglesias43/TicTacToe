package com.cracks.tictactoe

import android.util.Log

enum class PlayerAttribute {
    NAME, TOKEN
}

class Player {
    private var name: String? = null
    private var token: String? = null
    private var positions: MutableList<Int>

    constructor(name: String, token: String) {
        this.name = name
        this.token = token
        this.positions = mutableListOf()
    }

    fun addPosition(position: Int){
        this.positions.add(position);
    }

    fun getAttribute(type: PlayerAttribute) = when(type) {
        PlayerAttribute.NAME -> this.name!!
        PlayerAttribute.TOKEN -> this.token!!
    }

    fun setAttribute(type: PlayerAttribute, value: String) = when(type) {
        PlayerAttribute.NAME -> this.name = value
        PlayerAttribute.TOKEN -> this.token = value
    }

    fun getPositions(): MutableList<Int> {
        return this.positions
    }

    fun setPositions(newList: MutableList<Int>) {
        this.positions = newList
    }

    fun isWinner(): Boolean {
        for(position in positions) {
            Log.println(Log.INFO,"GG", "S: $position")
            if(position%3==0) if(positions.contains(position-1) && positions.contains(position-2)) return true
            if(position<4) if(positions.contains(position+3) && positions.contains(position+6)) return true
        }
        return (positions.contains(1) && positions.contains(5) && positions.contains(9))
                || (positions.contains(3) && positions.contains(5) && positions.contains(7))
    }
}