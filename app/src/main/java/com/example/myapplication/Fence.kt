package com.example.myapplication

class Fence constructor(name : String, status : Int){
    var name : String = name
    var status : Int = status

    fun inf() : String{
        return "door name: $name, status: $status"
    }

    fun switch() {
        status = (status + 1) % 2
    }
}