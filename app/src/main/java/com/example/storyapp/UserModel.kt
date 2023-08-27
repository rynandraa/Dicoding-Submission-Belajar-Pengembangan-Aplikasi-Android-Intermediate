package com.example.storyapp

data class UserModel (
    val name: String,
    val email: String,
    val password: String,
    val isLogin: Boolean
)

data class UserToken (
    val token: String
)