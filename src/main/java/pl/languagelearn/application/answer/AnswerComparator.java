package pl.languagelearn.application.answer;

import java.util.Comparator;

public class AnswerComparator implements Comparator<Answer> {

    @Override
    public int compare(Answer o1, Answer o2) {
        if(o1.getTime().isBefore(o2.getTime()))
            return -1;
        if(o1.getTime().isAfter(o2.getTime()))
            return 1;
        return 0;
    }
}
