package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            // Чтение данных
            Gson gson = new Gson();
            Type typeToken = new TypeToken<List<Visitor>>() {}.getType();
            List<Visitor> visitors = gson.fromJson(new FileReader("books.json"), typeToken);

            // Задание 1: Список посетителей
            System.out.println("=== ЗАДАНИЕ 1 ===");
            System.out.println("Список посетителей (" + visitors.size() + "):");
            visitors.forEach(v -> System.out.printf("- %s %s (тел.: %s)%n",
                    v.getName(), v.getSurname(), v.getPhone()));

            // Задание 2: Уникальные книги
            System.out.println("\n=== ЗАДАНИЕ 2 ===");
            List<Book> uniqueBooks = visitors.stream()
                    .flatMap(v -> v.getBooks().stream())
                    .distinct()
                    .collect(Collectors.toList());

            System.out.println("Уникальные книги в избранном (" + uniqueBooks.size() + "):");
            uniqueBooks.forEach(b -> System.out.printf("- %s (%s, %d)%n",
                    b.getName(), b.getAuthor(), b.getPublishingYear()));

            // Задание 3: Сортировка книг по году
            System.out.println("\n=== ЗАДАНИЕ 3 ===");
            System.out.println("Книги, отсортированные по году издания:");
            uniqueBooks.stream()
                    .sorted(Comparator.comparingInt(Book::getPublishingYear))
                    .forEach(b -> System.out.printf("- %d: %s (%s)%n",
                            b.getPublishingYear(), b.getName(), b.getAuthor()));

            // Задание 4: Найти посетителей с книгами Jane Austen
            System.out.println("\n=== ЗАДАНИЕ 4 ===");
            List<Visitor> janeAustenFans = visitors.stream()
                    .filter(visitor -> visitor.getBooks().stream()
                            .anyMatch(book -> "Jane Austen".equals(book.getAuthor())))
                    .toList();

            if (janeAustenFans.isEmpty()) {
                System.out.println("Книги Jane Austen ни у кого не в избранном");
            } else {
                System.out.println("Книги Jane Austen есть у следующих посетителей:");
                janeAustenFans.forEach(visitor -> {
                    System.out.print("- " + visitor.getName() + " " + visitor.getSurname());

                    List<String> janeAustenBooks = visitor.getBooks().stream()
                            .filter(book -> "Jane Austen".equals(book.getAuthor()))
                            .map(Book::getName)
                            .toList();

                    System.out.println(" (книги: " + String.join(", ", janeAustenBooks) + ")");
                });
            }

            // Задание 5: Найти максимальное число книг в избранном
            System.out.println("\n=== ЗАДАНИЕ 5 ===");
            int maxBooksCount = visitors.stream()
                    .mapToInt(visitor -> visitor.getBooks().size())
                    .max()
                    .orElse(0);

            System.out.println("Максимальное число книг в избранном: " + maxBooksCount);

            System.out.println("Посетители с максимальным числом книг (" + maxBooksCount + "):");
            visitors.stream()
                    .filter(visitor -> visitor.getBooks().size() == maxBooksCount)
                    .forEach(visitor -> System.out.println("- " + visitor.getName() + " " + visitor.getSurname()));

            // Задание 6: SMS-рассылка
            System.out.println("\n=== ЗАДАНИЕ 6 ===");
            List<Visitor> subscribedVisitors = visitors.stream()
                    .filter(Visitor::isSubscribed)
                    .toList();

            if (subscribedVisitors.isEmpty()) {
                System.out.println("Нет подписанных посетителей для рассылки");
                return;
            }

            double averageBooks = subscribedVisitors.stream()
                    .mapToInt(v -> v.getBooks().size())
                    .average()
                    .orElse(0);
            System.out.printf("Среднее количество книг: %.1f\n", averageBooks);

            // Создаем SMS сообщения
            List<SMS> smsList = subscribedVisitors.stream()
                    .map(v -> createSmsForVisitor(v, averageBooks))
                    .toList();

            // Группировка SMS по категориям
            Map<String, List<SMS>> groupedSms = smsList.stream()
                    .collect(Collectors.groupingBy(sms -> {
                        if (sms.getMessage().contains("bookworm")) return "Bookworms";
                        else if (sms.getMessage().contains("more")) return "Need to read more";
                        else return "Average readers";
                    }));

            // Вывод результатов
            System.out.println("\nВсе SMS сообщения:");
            smsList.forEach(System.out::println);

            System.out.println("\nГруппировка по категориям:");
            groupedSms.forEach((category, smses) -> {
                System.out.println("\n--- " + category + " (" + smses.size() + ") ---");
                smses.forEach(System.out::println);
            });

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static SMS createSmsForVisitor(Visitor visitor, double average) {
        int count = visitor.getBooks().size();
        String message;

        if (count > average) {
            message = "You are a bookworm! Your collection: " + count + " books";
        } else if (count < average) {
            message = "Read more! You have only " + count + " books";
        } else {
            message = "Fine! You have average " + count + " books";
        }

        return new SMS(visitor.getPhone(), message);
    }
}
