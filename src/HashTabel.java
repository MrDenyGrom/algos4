import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HashTabel {

    private static final int TABLE_SIZE = 2500;
    private static final String KEY_FORMAT = "\\d{2}[A-Z]{2}\\d{2}";
    private final HashNode[] table;

    public HashTabel() {
        table = new HashNode[TABLE_SIZE];
    }

    private static class HashNode {
        List<String> keys;
        String value;
        boolean isDeleted;

        HashNode(String key, String value) {
            this.keys = new ArrayList<>();
            this.keys.add(key);
            this.value = value;
            this.isDeleted = false;
        }

        public void addKey(String key) {
            this.keys.add(key);
        }
    }

    private int hash1(String key) {
        int hash = 0;
        for (char ch : key.toCharArray()) {
            hash = (hash * 31 + ch) % TABLE_SIZE;
        }
        return hash;
    }

    private int hash2(String key) {
        int hash = 0;
        for (char ch : key.toCharArray()) {
            hash = (hash * 17 + ch) % (TABLE_SIZE - 1) + 1;
        }
        return hash;
    }

    public void insert(String key, String value) {
        if (!validateKeyFormat(key)) {
            System.out.println("Неверный формат ключа! Ожидаемый формат: ццББцц");
            return;
        }

        int index = hash1(key);
        int step = hash2(key);

        for (int i = 0; i < TABLE_SIZE; i++) {
            if (table[index] == null) {
                table[index] = new HashNode(key, value);
                return;
            }
            if (!table[index].keys.contains(key)) {
                table[index].addKey(key);
                return;
            }
            index = (index + step) % TABLE_SIZE;
        }
        System.out.println("Ошибка: Таблица переполнена");
    }

    public String search(String key) {
        if (!validateKeyFormat(key)) {
            System.out.println("Неверный формат ключа! Ожидаемый формат: ццББцц");
            return null;
        }

        int index = hash1(key);
        int step = hash2(key);

        for (int i = 0; i < TABLE_SIZE; i++) {
            if (table[index] == null) {
                return null;
            }
            if (table[index].keys.contains(key) && !table[index].isDeleted) {
                return table[index].value;
            }
            index = (index + step) % TABLE_SIZE;
        }
        return null;
    }

    public void remove(String key) {
        if (!validateKeyFormat(key)) {
            System.out.println("Неверный формат ключа! Ожидаемый формат: ццББцц");
            return;
        }

        int index = hash1(key);
        int step = hash2(key);

        for (int i = 0; i < TABLE_SIZE; i++) {
            if (table[index] == null) {
                System.out.println("Элемент не найден.");
                return;
            }
            if (table[index].keys.contains(key) && !table[index].isDeleted) {
                table[index].keys.remove(key);
                if (table[index].keys.isEmpty()) {
                    table[index].isDeleted = true;
                }
                System.out.println("Элемент удален.");
                return;
            }
            index = (index + step) % TABLE_SIZE;
        }
        System.out.println("Элемент не найден.");
    }

    public boolean validateKeyFormat(String key) {
        return key.matches(KEY_FORMAT);
    }

    private String generateRandomKey() {
        Random random = new Random();
        return String.format("%02d%c%c%02d",
                random.nextInt(90) + 10,
                random.nextInt(26) + 'A',
                random.nextInt(26) + 'A',
                random.nextInt(90) + 10);
    }

    public void exportToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Index;Count\n");
            for (int i = 0; i < TABLE_SIZE; i++) {
                if (table[i] != null && !table[i].isDeleted) {
                    writer.write(i + ";" + table[i].keys.size() + "\n");
                }
            }
            System.out.println("Данные успешно выгружены в файл: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте данных в файл: " + e.getMessage());
        }
    }

    public void generateRandomData(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String key = generateRandomKey();
            String value = String.valueOf(random.nextInt(1000));
            insert(key, value);
        }
    }

    public void display() {
        System.out.println("\n----------------------------------------");
        System.out.println("| Индекс |         Ключи         | Значение |");
        System.out.println("----------------------------------------");
        for (int i = 0; i < TABLE_SIZE; i++) {
            if (table[i] != null && !table[i].isDeleted) {
                System.out.printf("| %6d | %20s | %8s |\n", i, String.join(",", table[i].keys), table[i].value);
            }
        }
        System.out.println("----------------------------------------");
    }

    public void clearTable() {
        for (int i = 0; i < TABLE_SIZE; i++) {
            table[i] = null;
        }
        System.out.println("Таблица очищена.");
    }

    private static void displayMenu() {
        System.out.println("\nМеню:");
        System.out.println("1. Добавить элемент");
        System.out.println("2. Найти элемент");
        System.out.println("3. Удалить элемент");
        System.out.println("4. Показать хеш-таблицу");
        System.out.println("5. Экспортировать хеш-таблицу в файл");
        System.out.println("6. Очистить таблицу");
        System.out.println("7. Сгенерировать случайные данные");
        System.out.println("0. Выйти");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashTabel hashTable = new HashTabel();

        while (true) {
            displayMenu();
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Введите ключ (формат ццББцц): ");
                    String key = scanner.next();
                    System.out.print("Введите значение: ");
                    String value = scanner.next();
                    hashTable.insert(key, value);
                    break;
                case 2:
                    System.out.print("Введите ключ для поиска (формат ццББцц): ");
                    String searchKey = scanner.next();
                    String foundValue = hashTable.search(searchKey);
                    System.out.println(foundValue != null ? "Найдено значение: " + foundValue : "Элемент не найден.");
                    break;
                case 3:
                    System.out.print("Введите ключ для удаления (формат ццББцц): ");
                    String removeKey = scanner.next();
                    hashTable.remove(removeKey);
                    break;
                case 4:
                    hashTable.display();
                    break;
                case 5:
                    System.out.print("Введите имя файла для экспорта: ");
                    String filename = scanner.next();
                    hashTable.exportToFile(filename);
                    break;
                case 6:
                    hashTable.clearTable();
                    break;
                case 7:
                    System.out.print("Введите количество случайных элементов: ");
                    int count = scanner.nextInt();
                    hashTable.generateRandomData(count);
                    break;
                case 0:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
            }
        }
    }
}
