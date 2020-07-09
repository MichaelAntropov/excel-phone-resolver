package com.hizencode.phoneresolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PhoneResolver {

    public static final String ALL_CHARS_EXCEPT_SEMICOLON_REGEX = "[^0-9;]*";
    public static final String OPERATOR_CHECK_REGEX =
            "^(39|67|68|96|97|98|50|66|95|99|63|93|91|92|94)(.*)";

    public static final String UKRAINIAN_PHONE_CODE = "+380";

    public static final String SPLITERATOR = ";";

    public static PhoneResult<String, String> resolve(String phoneString) {

        PhoneResult<String, String> result = new PhoneResult<>("", "");

        //Standard entry check
        if(phoneString == null || phoneString.isBlank() || phoneString.isEmpty()) {
            return result;
        }

        //Deleting aa chars except digits and ";"]
        phoneString =
                phoneString.replaceAll(ALL_CHARS_EXCEPT_SEMICOLON_REGEX, "");

        //Splitting by ";"
        String[] phoneStringArr = phoneString.split(SPLITERATOR);

        //Return if an array is nothing
        // eg. ;gre;ert;ret; -> Empty array, after removing non digit chars
        if(phoneStringArr.length == 0) {
            return result;
        }

        //Filtering those which are >= 9 getting last 9 chars and check an operator
        //if set is empty -> return
        Set<String> resultSet = new HashSet<>();

        for(String string: phoneStringArr) {
            if(string.length() >= 9) {
                string = string.substring(string.length() - 9);

                if(!string.matches(OPERATOR_CHECK_REGEX)) {
                    continue;
                }

                resultSet.add(string);
            }
        }

        if(resultSet.isEmpty()) {
            return result;
        }

        //Now we iterate through set and first element we add in mainResult,
        // others in secondaryResult
        Iterator<String> iterator = resultSet.iterator();


        result.setMainResult(UKRAINIAN_PHONE_CODE + iterator.next());

        iterator.forEachRemaining(phone ->
                result.setSecondaryResult(result.getSecondaryResult() + UKRAINIAN_PHONE_CODE + phone + SPLITERATOR));

        //Useful method in String to remove las char? Nah..
        // Dont know such things...Don't wanna use external libs tho
        if(result.getSecondaryResult().length() >= 1) {
            result.setSecondaryResult(
                    result.getSecondaryResult()
                            .substring(0, result.getSecondaryResult().length() - 1));
        }

        return result;
    }
}
