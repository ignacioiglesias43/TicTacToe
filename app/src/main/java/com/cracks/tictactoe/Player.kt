package com.cracks.tictactoe

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

        }
        return true
    }
}