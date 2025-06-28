package models;

import java.util.Objects;

public class UserCreateAndEditRequest {
    private String email;
    private String password;
    private String name;

    public UserCreateAndEditRequest() {
    }

    public UserCreateAndEditRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCreateAndEditRequest that = (UserCreateAndEditRequest) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, name);
    }

    @Override
    public String toString() {
        return "UserCreateAndEditRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" + // Пароль не логируем для безопасности
                ", name='" + name + '\'' +
                '}';
    }
}