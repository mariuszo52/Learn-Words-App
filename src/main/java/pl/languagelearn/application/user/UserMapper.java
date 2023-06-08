package pl.languagelearn.application.user;

class UserMapper {

    public static UserDto map(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUserRole().getName(),
                user.isAccountNotLocked(),
                user.getConfirmationToken());
    }

    public static UserStatsDto mapToUserStatsDto(User user){
        UserStatsDto userStatsDto = new UserStatsDto();
        userStatsDto.setId(user.getId());
        userStatsDto.setDaysInARow(user.getDaysInARow());
        userStatsDto.setLearnedWords(user.getLearnedWords());
        userStatsDto.setNewWords(user.getNewWords());
        userStatsDto.setNewWordsWeek(user.getNewWordsWeek());
        userStatsDto.setRepeatedWords(user.getRepeatedWords());
        userStatsDto.setTimeToday(user.getTimeToday());
        userStatsDto.setRepeatedWordsToday(user.getRepeatedWordsToday());
        userStatsDto.setAllTime(user.getAllTime());
        userStatsDto.setWordsToLearn(user.getWordsToLearn());
        userStatsDto.setRegisterDate(user.getRegisterDate());
        userStatsDto.setLastLogin(user.getLastLogin());
        return userStatsDto;


    }
}
