package diplom2;

public class UserGenerator {
    public static User getDeffault() {
        return new User("alex_messi@yandex.ru", "12345password", "Cristiano");
    }
    public static User getChangedInfo(){
        return new User("alex_mes@yandex.ru", "12345pord", "Suarez");
    }
    public static User getWithoutName() {
        return new User("test1_test@yandex.ru");
    }
    public static User getWithoutEmail() {
        return new User("test2_test@yandex.ru", "password1345");
    }
    public static User getNotExistsLogin() {
        return new User("alexander_5001@yandex.ru", "1234", "Александр");
    }
}
