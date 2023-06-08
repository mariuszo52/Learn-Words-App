package pl.languagelearn.application.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.languagelearn.application.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class CategoryServiceTest {
    @Mock CategoryRepository categoryRepository;

    private CategoryService categoryService;
    private AutoCloseable autoCloseable;
    final Category category1 = new Category(1L, "Category 1", new User(1L, "1@mail.pl"));
    final Category category2 = new Category(2L, "Category 2", new User(2L, "2@mail.pl"));
    final Category category3 = new Category(3L, "Category 3", new User(3L, "3@mail.pl"));
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository);
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldFind2Categories(){
        //given
        List<Category> categories = List.of(category1, category2);
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
        //when
        List<CategoryDto> result = categoryService.findAll();
        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Category 1", result.get(0).getName());
        Assertions.assertEquals("Category 2", result.get(1).getName());
    }
    @Test
    void shouldNotFindAnyCategory(){
        //given
        ArrayList<Category> categories = new ArrayList<>();
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
        //when
        List<CategoryDto> result = categoryService.findAll();
        //then
        Assertions.assertEquals(0, result.size());
        assertThat(result).isEmpty();
    }
    @Test
    void shouldFindCategoryWithId2(){
        //given
        Mockito.when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));
        //when
        Optional<CategoryDto> resultCategory = categoryService.findCategoryById(2L);
        //then
        Assertions.assertTrue(resultCategory.isPresent());
        Assertions.assertEquals(2L, resultCategory.get().getId());
        Assertions.assertEquals("Category 2", resultCategory.get().getName());
    }
    @Test
    void shouldNotFindForId0(){
        //given
        Mockito.when(categoryRepository.findById(0L)).thenReturn(Optional.empty());
        //when
        Optional<CategoryDto> resultCategory = categoryService.findCategoryById(1L);
        //then
        Assertions.assertTrue(resultCategory.isEmpty());
    }
    @Test
    void shouldReturnEmptyList(){
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("user@user.pl");
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<Category> categories = List.of(category1, category2, category3);

        Mockito.when(categoryRepository.findCategoriesByUser_Email("user@wuser.com"))
                .thenReturn(categories);

        List<CategoryDto> allUserCategories = categoryService.findAllUserCategories();
        Assertions.assertTrue(allUserCategories.isEmpty());
    }
    @Test
    void shouldReturn1category(){
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("1@mail.pl");
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<Category> userCategories = List.of(category1);

        Mockito.when(categoryRepository.findCategoriesByUser_Email("1@mail.pl"))
                .thenReturn(userCategories);

        List<CategoryDto> allUserCategories = categoryService.findAllUserCategories();
        assertThat(allUserCategories.size()).isEqualTo(1);
    }

    @Test
    void shouldReturn2category(){
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("1@mail.pl");
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<Category> userCategories = List.of(category1, category2);

        Mockito.when(categoryRepository.findCategoriesByUser_Email("1@mail.pl"))
                .thenReturn(userCategories);

        List<CategoryDto> allUserCategories = categoryService.findAllUserCategories();
        assertThat(allUserCategories.size()).isEqualTo(2);
    }
}