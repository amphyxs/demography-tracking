package com.assistant.centralservice.repository;

import com.assistant.centralservice.model.Country;
import com.assistant.centralservice.model.Person;
import com.assistant.centralservice.model.HairColor;
import com.assistant.centralservice.model.EyeColor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class PersonRepository {

    @PersistenceContext(unitName = "primary")
    private EntityManager entityManager;

    public Person save(Person person) {
        if (person.getId() == null) {
            entityManager.persist(person);
            return person;
        } else {
            return entityManager.merge(person);
        }
    }

    public Optional<Person> findById(Integer id) {
        Person person = entityManager.find(Person.class, id);
        return Optional.ofNullable(person);
    }

    public List<Person> findAll() {
        return entityManager.createQuery("SELECT p FROM Person p", Person.class)
                .getResultList();
    }

    public void delete(Person person) {
        if (entityManager.contains(person)) {
            entityManager.remove(person);
        } else {
            entityManager.remove(entityManager.merge(person));
        }
    }

    public boolean deleteById(Integer id) {
        Optional<Person> person = findById(id);
        if (person.isPresent()) {
            delete(person.get());
            return true;
        }
        return false;
    }

    public List<Person> findWithFiltersAndSort(Map<String, Object> filters,
                                               List<String> sortParams,
                                               int page,
                                               int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> query = cb.createQuery(Person.class);
        Root<Person> root = query.from(Person.class);

        List<Predicate> predicates = buildPredicates(cb, root, filters);
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        List<Order> orders = buildOrders(cb, root, sortParams);
        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }

        TypedQuery<Person> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    public long countWithFilters(Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Person> root = query.from(Person.class);

        query.select(cb.count(root));

        List<Predicate> predicates = buildPredicates(cb, root, filters);
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    public Double getAverageWeight() {
        return entityManager.createQuery(
                        "SELECT AVG(p.weight) FROM Person p WHERE p.weight IS NOT NULL",
                        Double.class)
                .getSingleResult();
    }

    public long countByLocation(Float x, Integer y, String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Person> root = query.from(Person.class);

        query.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<>();

        if (x != null) {
            predicates.add(cb.equal(root.get("location").get("x"), x));
        }
        if (y != null) {
            predicates.add(cb.equal(root.get("location").get("y"), y));
        }
        if (name != null) {
            predicates.add(cb.equal(root.get("location").get("name"), name));
        }

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    public List<Person> findByHeightGreaterThan(double minHeight) {
        return entityManager.createQuery(
                        "SELECT p FROM Person p WHERE p.height > :minHeight",
                        Person.class)
                .setParameter("minHeight", minHeight)
                .getResultList();
    }

    /**
     * Метод, помогающий построить фильтры
     */
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Person> root, Map<String, Object> filters) {
        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            String key = filter.getKey();
            Object value = filter.getValue();

            if (value == null) continue;

            switch (key) {
                case "id":
                    predicates.add(cb.equal(root.get("id"), value));
                    break;
                case "id[lt]":
                    predicates.add(cb.lessThan(root.get("id"), (Integer) value));
                    break;
                case "id[gt]":
                    predicates.add(cb.greaterThan(root.get("id"), (Integer) value));
                    break;
                case "name":
                    predicates.add(cb.equal(root.get("name"), value));
                    break;
                case "height":
                    predicates.add(cb.equal(root.get("height"), value));
                    break;
                case "height[lt]":
                    predicates.add(cb.lessThan(root.get("height"), (Double) value));
                    break;
                case "height[gt]":
                    predicates.add(cb.greaterThan(root.get("height"), (Double) value));
                    break;
                case "weight":
                    predicates.add(cb.equal(root.get("weight"), value));
                    break;
                case "weight[lt]":
                    predicates.add(cb.lessThan(root.get("weight"), (Long) value));
                    break;
                case "weight[gt]":
                    predicates.add(cb.greaterThan(root.get("weight"), (Long) value));
                    break;
                case "nationality":
                    predicates.add(cb.equal(root.get("nationality"),
                            Country.valueOf((String) value)));
                    break;
                case "birthday":
                    predicates.add(cb.equal(root.get("birthday"), value));
                    break;
                case "birthday[lt]":
                    predicates.add(cb.lessThan(root.get("birthday"), (LocalDate) value));
                    break;
                case "birthday[gt]":
                    predicates.add(cb.greaterThan(root.get("birthday"), (LocalDate) value));
                    break;
            }
        }

        return predicates;
    }

    /**
     * Метод, помогающий назначить порядок сортировки
     */
    private List<Order> buildOrders(CriteriaBuilder cb, Root<Person> root, List<String> sortParams) {
        List<Order> orders = new ArrayList<>();

        if (sortParams == null || sortParams.isEmpty()) {
            orders.add(cb.asc(root.get("id")));
            return orders;
        }

        for (String sortParam : sortParams) {
            String[] parts = sortParam.split(",");
            if (parts.length != 2) continue;

            String field = parts[0].trim();
            boolean ascending = "asc".equalsIgnoreCase(parts[1].trim());

            Expression<?> expression = getExpression(root, field);
            if (expression != null) {
                orders.add(ascending ? cb.asc(expression) : cb.desc(expression));
            }
        }

        if (orders.isEmpty()) {
            orders.add(cb.asc(root.get("id")));
        }

        return orders;
    }

    private Expression<?> getExpression(Root<Person> root, String field) {
        switch (field) {
            case "id":
                return root.get("id");
            case "name":
                return root.get("name");
            case "height":
                return root.get("height");
            case "weight":
                return root.get("weight");
            case "birthday":
                return root.get("birthday");
            case "nationality":
                return root.get("nationality");
            case "coordinates.x":
                return root.get("coordinates").get("x");
            case "coordinates.y":
                return root.get("coordinates").get("y");
            case "location.x":
                return root.get("location").get("x");
            case "location.y":
                return root.get("location").get("y");
            case "location.name":
                return root.get("location").get("name");
            default:
                return null;
        }
    }

    public long getTotalCount() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Person p", Long.class)
                .getSingleResult();
    }

    public long countByHairColor(HairColor hairColor) {
        return entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.hairColor = :hairColor",
                        Long.class)
                .setParameter("hairColor", hairColor)
                .getSingleResult();
    }

    public double getPercentageByNationalityAndEyeColor(Country nationality, EyeColor eyeColor) {
        Long totalByNationality = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.nationality = :nationality",
                        Long.class)
                .setParameter("nationality", nationality)
                .getSingleResult();

        if (totalByNationality == 0) {
            return 0.0;
        }

        Long countByNationalityAndEyeColor = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.nationality = :nationality AND p.eyeColor = :eyeColor",
                        Long.class)
                .setParameter("nationality", nationality)
                .setParameter("eyeColor", eyeColor)
                .getSingleResult();

        return (double) countByNationalityAndEyeColor / totalByNationality * 100.0;
    }
}