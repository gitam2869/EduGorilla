package com.example.edugorilla.ModelClasses;

public class UserDataInfo
{
    public String id;
    public String name;
    public String email;

    public UserDataInfo(
            String id,
            String name,
            String email)
    {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
