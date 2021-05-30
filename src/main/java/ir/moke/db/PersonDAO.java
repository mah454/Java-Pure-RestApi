package ir.moke.db;

import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    private static final List<Person> PERSON_LIST = new ArrayList<>();

    public static final PersonDAO instance = new PersonDAO();

    private PersonDAO() {
        Person p1 = new Person(1, "mahdi", "hosseini");
        Person p2 = new Person(2, "Ali", "Mohammadi");
        Person p3 = new Person(3, "Javad", "abbasi");
        PERSON_LIST.add(p1);
        PERSON_LIST.add(p2);
        PERSON_LIST.add(p3);
    }

    public Person save(Person person) {
        person.setId(PERSON_LIST.get(PERSON_LIST.size() - 1).getId() + 1);
        PERSON_LIST.add(person);
        return person;
    }

    public Person getPerson(int id) {
        return PERSON_LIST.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Person> getAllPersons() {
        return PERSON_LIST;
    }

    public void delete(int id) {
        PERSON_LIST.removeIf(e -> e.getId() == id);
    }
}
