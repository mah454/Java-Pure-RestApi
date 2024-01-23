package ir.moke.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    private static final Logger logger = LoggerFactory.getLogger(PersonDAO.class);
    private static final List<Person> PERSON_LIST = new ArrayList<>();

    public static final PersonDAO instance = new PersonDAO();

    private PersonDAO() {
        logger.trace("Initialise database");
        Person p1 = new Person(1, "mahdi", "hosseini");
        Person p2 = new Person(2, "Ali", "Mohammadi");
        Person p3 = new Person(3, "Javad", "abbasi");
        save(p1);
        save(p2);
        save(p3);

        logger.trace("Print with System.out");
        PERSON_LIST.stream().map(Person::toString).forEach(System.out::println);
    }

    public Person save(Person person) {
        logger.trace("Add new person name:%s family:%s".formatted(person.getName(), person.getFamily()));
        person.setId(nextId());
        PERSON_LIST.add(person);
        return person;
    }

    public Person getPerson(int id) {
        return PERSON_LIST.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Person> getAllPersons() {
        logger.trace("Fetch all persons");
        return PERSON_LIST;
    }

    public void delete(int id) {
        logger.trace("Delete person with id %s".formatted(id));
        PERSON_LIST.removeIf(e -> e.getId() == id);
    }

    private static int nextId() {
        if (PERSON_LIST.isEmpty()) return 1;
        return PERSON_LIST.get(PERSON_LIST.size() - 1).getId() + 1;
    }
}
