package pl.languagelearn.application.category;

import java.util.Comparator;

public class CategoryComparator implements Comparator<CategoryDto> {

    @Override
    public int compare(CategoryDto o1, CategoryDto o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
