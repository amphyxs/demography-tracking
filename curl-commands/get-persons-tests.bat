chcp 65001 >nul
REM Тесты GET запросов для эндпоинта /central-service/api/persons
REM Базовый URL
set "BASE_URL=http://localhost:8080/central-service/api/persons"

echo === CURL тесты для GET /central-service/api/persons ===
echo.

REM 1. Базовый запрос без параметров
echo 1. Базовый запрос (первые 20 записей):
curl -X GET "%BASE_URL%" ^
  -H "Accept: application/json" ^
  -H "Content-Type: application/json"
echo.
echo.

REM 2. Пагинация
echo 2. Пагинация - страница 1, размер 5:
curl -X GET "%BASE_URL%?page=1&size=5" ^
  -H "Accept: application/json"
echo.
echo.

echo 3. Пагинация - страница 0, размер 10:
curl -X GET "%BASE_URL%?page=0&size=10" ^
  -H "Accept: application/json"
echo.
echo.

REM 3. Сортировка
echo 4. Сортировка по имени (по возрастанию):
curl -X GET "%BASE_URL%?sort=name" ^
  -H "Accept: application/json"
echo.
echo.

echo 5. Сортировка по имени (по убыванию):
curl -X GET "%BASE_URL%?sort=name,desc" ^
  -H "Accept: application/json"
echo.
echo.

echo 6. Сортировка по весу (по возрастанию):
curl -X GET "%BASE_URL%?sort=weight" ^
  -H "Accept: application/json"
echo.
echo.

echo 7. Сортировка по росту (по убыванию):
curl -X GET "%BASE_URL%?sort=height,desc" ^
  -H "Accept: application/json"
echo.
echo.

echo 8. Множественная сортировка (по весу по возрастанию, потом по имени по убыванию):
curl -X GET "%BASE_URL%?sort=weight&sort=name,desc" ^
  -H "Accept: application/json"
echo.
echo.

REM 4. Фильтрация по точным значениям
echo 9. Фильтр по конкретному ID:
curl -X GET "%BASE_URL%?id=1" ^
  -H "Accept: application/json"
echo.
echo.

echo 10. Фильтр по имени:
curl -X GET "%BASE_URL%?name=John" ^
  -H "Accept: application/json"
echo.
echo.

echo 11. Фильтр по национальности:
curl -X GET "%BASE_URL%?nationality=RUSSIA" ^
  -H "Accept: application/json"
echo.
echo.

echo 12. Фильтр по точному росту:
curl -X GET "%BASE_URL%?height=180.5" ^
  -H "Accept: application/json"
echo.
echo.

echo 13. Фильтр по точному весу:
curl -X GET "%BASE_URL%?weight=75" ^
  -H "Accept: application/json"
echo.
echo.

REM 5. Фильтрация по диапазонам (больше/меньше)
echo 14. Фильтр ID больше 5:
curl -X GET "%BASE_URL%?id\[gt\]=5" ^
  -H "Accept: application/json"
echo.
echo.

echo 15. Фильтр ID меньше 10:
curl -X GET "%BASE_URL%?id\[lt\]=10" ^
  -H "Accept: application/json"
echo.
echo.

echo 16. Фильтр рост больше 170:
curl -X GET "%BASE_URL%?height\[gt\]=170" ^
  -H "Accept: application/json"
echo.
echo.

echo 17. Фильтр рост меньше 180:
curl -X GET "%BASE_URL%?height\[lt\]=180" ^
  -H "Accept: application/json"
echo.
echo.

echo 18. Фильтр вес больше 70:
curl -X GET "%BASE_URL%?weight\[gt\]=70" ^
  -H "Accept: application/json"
echo.
echo.

echo 19. Фильтр вес меньше 80:
curl -X GET "%BASE_URL%?weight\[lt\]=80" ^
  -H "Accept: application/json"
echo.
echo.

REM 6. Фильтрация по дате рождения
echo 20. Фильтр по точной дате рождения:
curl -X GET "%BASE_URL%?birthday=1990-01-01" ^
  -H "Accept: application/json"
echo.
echo.

echo 21. Фильтр дата рождения после 1985-01-01:
curl -X GET "%BASE_URL%?birthday\[gt\]=1985-01-01" ^
  -H "Accept: application/json"
echo.
echo.

echo 22. Фильтр дата рождения до 2000-01-01:
curl -X GET "%BASE_URL%?birthday\[lt\]=2000-01-01" ^
  -H "Accept: application/json"
echo.
echo.

REM 7. Комбинированные фильтры
echo 23. Комбинированный фильтр: рост больше 170 И вес меньше 80:
curl -X GET "%BASE_URL%?height\[gt\]=170&weight\[lt\]=80" ^
  -H "Accept: application/json"
echo.
echo.

echo 24. Комбинированный фильтр: ID от 1 до 10:
curl -X GET "%BASE_URL%?id\[gt\]=1&id\[lt\]=10" ^
  -H "Accept: application/json"
echo.
echo.

echo 25. Комбинированный фильтр с сортировкой: национальность RUSSIA, сортировка по весу:
curl -X GET "%BASE_URL%?nationality=RUSSIA&sort=weight" ^
  -H "Accept: application/json"
echo.
echo.

REM 8. Комбинированные фильтры с пагинацией и сортировкой
echo 26. Сложный запрос: рост больше 175, вес от 70 до 90, сортировка по имени, страница 0, размер 5:
curl -X GET "%BASE_URL%?height\[gt\]=175&weight\[gt\]=70&weight\[lt\]=90&sort=name&page=0&size=5" ^
  -H "Accept: application/json"
echo.
echo.

echo 27. Сложный запрос: люди родившиеся после 1980, с национальностью RUSSIA, сортировка по дате рождения по убыванию:
curl -X GET "%BASE_URL%?birthday\[gt\]=1980-01-01&nationality=RUSSIA&sort=birthday,desc" ^
  -H "Accept: application/json"
echo.
echo.

REM 9. Тесты граничных случаев
echo 28. Пустой результат (несуществующий ID):
curl -X GET "%BASE_URL%?id=99999" ^
  -H "Accept: application/json"
echo.
echo.

echo 29. Максимальный размер страницы (100):
curl -X GET "%BASE_URL%?size=100" ^
  -H "Accept: application/json"
echo.
echo.

echo 30. Тест некорректного размера страницы (больше 100) - должен вернуть 400:
curl -X GET "%BASE_URL%?size=101" ^
  -H "Accept: application/json"
echo.
echo.

echo 31. Тест некорректного размера страницы (0) - должен вернуть 400:
curl -X GET "%BASE_URL%?size=0" ^
  -H "Accept: application/json"
echo.
echo.

echo === Тесты завершены ===
pause