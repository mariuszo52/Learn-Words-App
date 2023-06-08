package pl.languagelearn.application.word.learnWords;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.languagelearn.application.answer.Answer;
import pl.languagelearn.application.answer.AnswerComparator;
import pl.languagelearn.application.exception.WordNotFoundException;
import pl.languagelearn.application.user.UserService;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordMapper;
import pl.languagelearn.application.word.WordRepository;
import pl.languagelearn.application.word.dto.WordDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
class LearnWordsService {
    private static final int MAX_PRIORITY = 1;
    private static final int MIN_PRIORITY = 5;
    private final WordRepository wordRepository;
    Set<Answer> repeatResults;
    HashSet<Long> repeatedWords;
    private int wordsCounter;
    private int repeat;


    LearnWordsService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        repeatResults = new TreeSet<>(new AnswerComparator());
        repeatedWords = new HashSet<>();
    }


    List<WordDto> createWordsToRepeat(String language) {
        Long loggedUserId = UserService.getLoggedUserId();
        return wordRepository.findWordsByPriorityAndUser_Id(MAX_PRIORITY, loggedUserId).stream()
                .filter(word -> word.getLanguage().getName().equals(language))
                .map(WordMapper::map)
                .collect(Collectors.toList());
    }

    Optional<WordDto> generateWordToRepeat(List<WordDto> words) {
        WordDto wordDto = null;
        if(!words.isEmpty()) {
            Collections.shuffle(words);
            wordDto = words.get(0);
        }
        return Optional.ofNullable(wordDto);
    }
    @Transactional
    public void updatePriority(Long wordId) {
        Word updatedWord = wordRepository.findById(wordId)
                .orElseThrow(WordNotFoundException::new);
        if (updatedWord.getPriority() < MIN_PRIORITY) {
            updatedWord.setPriority(updatedWord.getPriority() + 1);
            updatedWord.setLastRepeat();
            if(updatedWord.getRepeatCounter() != null) {
                updatedWord.setRepeatCounter(updatedWord.getRepeatCounter() + 1);
            }else {
                updatedWord.setRepeatCounter(1L);
            }
        }
    }
    public Optional<WordDto> getWordToRepeat(List<Long> words){
        long wordDtoId = words.get(0);
            while(repeatedWords.contains(wordDtoId)) {
                Collections.shuffle(words);
                wordDtoId = words.get(0);
                if (words.size() == repeatedWords.size()) {
                    return Optional.empty();
                }
            }
           return wordRepository.findById(wordDtoId)
                    .map(WordMapper::map);
    }

    public void resetRepeatProgress(HttpServletRequest request) {
        String currentUrl = request.getRequestURL().append("?").append(request.getQueryString()).toString();
        String referer = request.getHeader("Referer");
        if(referer == null || !referer.equals(currentUrl)){
           repeatedWords.clear();
           repeatResults.clear();
           wordsCounter = 0;
           repeat = 0;
        }
    }
    public String setDefaultAnswer(String answer) {
        if(answer.isEmpty()){
            answer = "Brak odpowiedzi";
        }
        return answer;
    }
    public List<Long> getWordsByCategoryAndLanguage(String language, long categoryId) {
        Long loggedUserId = UserService.getLoggedUserId();
        List<Long> words = wordRepository.findAllByUser_id(loggedUserId).stream()
                .filter(word -> word.getCategory().getId() == categoryId)
                .filter(word -> word.getLanguage().getName().equals(language))
                .map(Word::getId)
                .collect(Collectors.toList());
        Collections.shuffle(words);
        return words;
    }

    public long getNumberWordsToRepeatToday(String language) {
        Long loggedUserId = UserService.getLoggedUserId();
        return wordRepository.findAllByUser_id(loggedUserId).stream()
                .filter(word -> word.getPriority() == MAX_PRIORITY)
                .filter(word -> word.getLanguage().getName().equals(language))
                .count();


    }

    boolean checkAnswer(WordDto wordDto, String answer){
            return wordDto.getTranslation().equalsIgnoreCase(answer);

    }
    Set<Answer> findGoodAnswers(Set<Answer> answers){
       return answers.stream()
                .filter(Answer::isGoodAnswer)
                .collect(Collectors.toSet());
    }

    double calculateProgressOfRepetition(int wordNumber, int numbersWordsToRepeat){
        return (double) wordNumber / numbersWordsToRepeat * 100;

    }

    public HashSet<Long> getRepeatedWords() {
        return repeatedWords;
    }

    public int getWordsCounter() {
        return wordsCounter;
    }

    public void setWordsCounter(int wordsCounter) {
        this.wordsCounter = wordsCounter;
    }


    public Set<Answer> getRepeatResults() {
        return repeatResults;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
}
