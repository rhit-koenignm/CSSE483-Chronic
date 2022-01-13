package edu.rosehulman.chronic.models

class Tag() {
    lateinit var title: String
    lateinit var type: String
    var isTracked: Boolean = false

    constructor(title: String, type: String) : this() {
        this.title = title
        this.type = type
    }
}