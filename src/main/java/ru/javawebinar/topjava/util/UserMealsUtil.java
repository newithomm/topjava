package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на гра   ничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles

        if (meals == null)
            return new ArrayList<>();

        final Map<LocalDate, Integer> map = new HashMap<>();
        final Queue<UserMeal> tempUserMeals = new ArrayDeque<>();
        final List<UserMealWithExcess> userMealWithExcesses = new ArrayList<>();
        int i = 0, k = meals.size(), j = meals.size();

        while (i < j) {
            if (i < k) {
                map.putIfAbsent(meals.get(i).getDateTime().toLocalDate(), 0);
                int finalI = i;
                map.computeIfPresent(meals.get(i).getDateTime().toLocalDate(), (key, val) -> val + meals.get(finalI).getCalories());
                if (TimeUtil.isBetweenHalfOpen(meals.get(i).getDateTime().toLocalTime(), startTime, endTime)) {
                    tempUserMeals.add(meals.get(i));
                    j++;
                }
            } else {
                UserMeal tempMeal = tempUserMeals.poll();
                userMealWithExcesses.add(new UserMealWithExcess(
                        tempMeal.getDateTime(),
                        tempMeal.getDescription(),
                        tempMeal.getCalories(),
                        map.get(tempMeal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
            i++;
        }
        return userMealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        if (meals.isEmpty())
            return new ArrayList<>();

        Map<LocalDate, Integer> map = new HashMap<>();
        List<UserMeal> filteredMeals =
                meals
                        .stream()
                        .filter(meal -> {
                            map.putIfAbsent(meal.getDateTime().toLocalDate(), 0);
                            map.computeIfPresent(meal.getDateTime().toLocalDate(), (key, val) -> val + meal.getCalories());
                            return TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime);
                        }).collect(Collectors.toList());

        return filteredMeals.stream().map(filteredUserMeal ->
            new UserMealWithExcess(
                        filteredUserMeal.getDateTime(),
                        filteredUserMeal.getDescription(),
                        filteredUserMeal.getCalories(),
                        map.get(filteredUserMeal.getDateTime().toLocalDate()) > caloriesPerDay)
                ).collect(Collectors.toList());

    }
}
