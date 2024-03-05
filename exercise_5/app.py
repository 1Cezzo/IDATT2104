import subprocess
from flask import Flask, render_template, request

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/run', methods=['POST'])
def run_code():
    user_code = request.form['code']

    # Build and run a Docker container
    try:
        docker_command = f"docker run --rm python:3.11.7 python -c '{user_code}'"
        result = subprocess.run(docker_command, shell=True, capture_output=True, text=True)
        output = result.stdout.strip()
        error_message = result.stderr.strip() if result.stderr else None
    except Exception as e:
        error_message = str(e)
        output = None

    return render_template('result.html', result=output, error_message=error_message)

if __name__ == '__main__':
    app.run(host='0.0.0.0')
