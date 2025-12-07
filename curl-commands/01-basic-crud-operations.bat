# Базовые cURL команды для тестирования CRUD операций
# Убедитесь что сервер запущен на http://localhost:8080

BASE_URL="http://localhost:8080/central-service/api/persons"
CONTENT_TYPE="Content-Type: application/json"

echo "=== Тестирование базовых CRUD операций ==="
echo

# 1. Создание нового человека (POST)
echo "1. Создание нового человека:"
curl -X POST "$BASE_URL" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Тестовый Пользователь",
    "coordinates": {
      "x": 55.7558,
      "y": 37.6176
    },
    "height": 175.5,
    "birthday": "1990-05-15",
    "weight": 70,
    "nationality": "RUSSIA",
    "location": {
      "x": 55.7558,
      "y": 37,
      "name": "Москва"
    }
  }'
echo -e "\n"

# 2. Создание человека без веса (вес может быть null)
echo "2. Создание человека без веса:"
curl -X POST "$BASE_URL" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Человек Без Веса",
    "coordinates": {
      "x": 59.9311,
      "y": 30.3609
    },
    "height": 165.0,
    "birthday": "1985-12-08",
    "nationality": "RUSSIA",
    "location": {
      "x": 59.9311,
      "y": 30,
      "name": "Санкт-Петербург"
    }
  }'
echo -e "\n"

# 3. Создание человека с китайским именем
echo "3. Создание человека с китайским именем:"
curl -X POST "$BASE_URL" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "李明",
    "coordinates": {
      "x": 39.9042,
      "y": 116.4074
    },
    "height": 168.3,
    "birthday": "1987-09-12",
    "weight": 68,
    "nationality": "CHINA",
    "location": {
      "x": 39.9042,
      "y": 116,
      "name": "北京"
    }
  }'
echo -e "\n"

# 4. Получение всех людей (GET)
echo "4. Получение всех людей (первые 5):"
curl -X GET "$BASE_URL?size=5"
echo -e "\n"

# 5. Получение человека по ID (GET)
echo "5. Получение человека по ID=1:"
curl -X GET "$BASE_URL/1"
echo -e "\n"

# 6. Получение несуществующего человека
echo "6. Получение несуществующего человека (ID=999):"
curl -X GET "$BASE_URL/999"
echo -e "\n"

# 7. Обновление человека (PUT)
echo "7. Обновление человека с ID=1:"
curl -X PUT "$BASE_URL/1" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Обновленный Пользователь",
    "coordinates": {
      "x": 56.8431,
      "y": 60.6454
    },
    "height": 180.0,
    "birthday": "1990-05-15",
    "weight": 85,
    "nationality": "RUSSIA",
    "location": {
      "x": 56.8431,
      "y": 60,
      "name": "Екатеринбург"
    }
  }'
echo -e "\n"

# 8. Попытка обновить несуществующего человека
echo "8. Попытка обновить несуществующего человека (ID=999):"
curl -X PUT "$BASE_URL/999" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "Несуществующий",
    "coordinates": {
      "x": 50.0,
      "y": 50.0
    },
    "height": 170.0,
    "birthday": "1990-01-01",
    "nationality": "RUSSIA",
    "location": {
      "x": 50.0,
      "y": 50,
      "name": "Тест"
    }
  }'
echo -e "\n"

# 9. Получение специальных эндпоинтов - средний вес
echo "9. Получение среднего веса:"
curl -X GET "$BASE_URL/average-weight"
echo -e "\n"

# 10. Получение людей выше определенного роста
echo "10. Получение людей выше 170 см:"
curl -X GET "$BASE_URL/by-height?minHeight=170"
echo -e "\n"

# 11. Подсчет людей по местоположению
echo "11. Подсчет людей в Москве:"
curl -X GET "$BASE_URL/count-by-location?name=Москва"
echo -e "\n"

# 12. Удаление человека (DELETE) - закомментировано чтобы не удалять данные
echo "12. Удаление человека с ID=1 (закомментировано):"
echo "# curl -X DELETE \"$BASE_URL/1\""
echo -e "\n"

# 13. Попытка удалить несуществующего человека
echo "13. Попытка удалить несуществующего человека (ID=999):"
curl -X DELETE "$BASE_URL/999"
echo -e "\n"

echo "=== Тестирование завершено ==="