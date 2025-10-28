package org.example.validator;

import org.example.model.Entry;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class EntryValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Entry.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Entry entry = (Entry) target;

        if(entry.getTopSpeed() == null || entry.getTopSpeed().signum() <=0){
            errors.rejectValue("topSpeed", "entry's top speed must be greater than zero");
        }
        if(entry.getAverageSpeed() == null || entry.getAverageSpeed().signum() <=0){
            errors.rejectValue("averageSpeed","entry's average speed must be greater than or equal to 0");
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}
