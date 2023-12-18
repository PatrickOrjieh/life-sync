from app import app, start_background_task

if __name__ == "__main__":
    start_background_task(app)
    app.run(debug=True)
