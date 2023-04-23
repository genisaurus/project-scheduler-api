package com.russell.scheduler.common;

import com.russell.scheduler.exceptions.InvalidRequestException;
import com.russell.scheduler.exceptions.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class EntitySearcher {

    private final EntityManager entityManager;

    @Autowired
    public EntitySearcher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> Set<T> search(Map<String, String> searchCriteria, Class<T> entityClass) {
        if (entityClass.getAnnotation(Entity.class) == null)
            throw new InvalidRequestException(entityClass.getSimpleName() + " is not a searchable entity");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        Predicate predicate = cb.conjunction();

        for(Map.Entry<String, String> term : searchCriteria.entrySet()) {
            String key = term.getKey();
            String value = term.getValue();

            try {
                if (key.contains(".")) {
                    String[] keyFrags = key.split("\\.");
                    String nestedTypeName = keyFrags[0];
                    String nestedTypeFieldName = keyFrags[1];
                    Join joinType = root.join(nestedTypeName);
                    Field nestedTypeField = entityClass
                            .getDeclaredField(nestedTypeName)
                            .getClass()
                            .getDeclaredField((nestedTypeFieldName));
                    predicate = getPredicate(cb, predicate, value, nestedTypeField, joinType.get(nestedTypeFieldName));
                } else {
                    Field searchField = entityClass.getDeclaredField(key);
                    predicate = getPredicate(cb, predicate, value, searchField, root.get(key));
                }
            } catch (NoSuchFieldException e) {
                throw new InvalidRequestException("Attribute " + key + " does not exist on entity " + entityClass.getSimpleName());
            }
        }

        query.where(predicate);

        return new HashSet<>(entityManager.createQuery(query).getResultList());
    }

    private Predicate getPredicate(CriteriaBuilder cb, Predicate pred, String value, Field searchField, Path path) {
        if (searchField.getType().isEnum()) {
            try {
                Enum enumVal = Enum.valueOf((Class<Enum>) searchField.getType(), value.toUpperCase());
                pred = cb.and(pred, cb.equal(path, enumVal));
            } catch (IllegalArgumentException e) {
                throw new RecordNotFoundException();
            }
        } else
            pred = cb.and(pred, cb.equal(path, value));

        return pred;
    }
}
