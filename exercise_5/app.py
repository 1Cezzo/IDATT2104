from flask import Flask, render_template, request
import docker

app = Flask(__name__)
client = docker.from_env()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/run', methods=['POST'])
def run_code():
    user_code = request.form['code']

    # Create a Docker container
    try:
        container = client.containers.run(
            'code-runner',  # Name of the Docker image
            command='python -c "{}"'.format(user_code),
            remove=True,
            detach=True
        )

        # Capture the output of the Docker container
        result = container.decode('utf-8')
        error_message = None
    except docker.errors.APIError as e:
        # Handle Docker API errors
        error_message = str(e)
        result = None

    return render_template('result.html', result=result, error_message=error_message)

if __name__ == '__main__':
    app.run(host='0.0.0.0')