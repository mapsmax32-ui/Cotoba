# KOTOBA Android — сборка APK без Android Studio

## Самый простой способ
1. Создай аккаунт на GitHub: https://github.com/
2. Создай новый репозиторий, например `kotoba-android`.
3. Нажми **Add file → Upload files**.
4. Загрузи содержимое этой папки целиком, включая `.github`.
5. Нажми **Commit changes**.
6. Открой вкладку **Actions**.
7. Выбери **Build KOTOBA APK** и нажми **Run workflow**.
8. После окончания сборки открой запуск workflow и внизу найди **Artifacts → KOTOBA-debug-apk**.
9. Скачай ZIP, распакуй его на телефон и установи `app-debug.apk`.

## Совместимость
Минимальная версия Android: 8.0 (API 26).

Это первая Android-версия интерфейса KOTOBA. AI пока подключается через будущий backend; демо-диалог уже есть в приложении.

<!-- build trigger: functional navigation -->
