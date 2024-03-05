from flask import Flask, render_template, request

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/compile', methods=['POST'])
def compile_code():
    code = request.form['code']  # Get the source code from the form
    
    # Here you would pass the code to the Docker container for compilation
    # You can use subprocess or other libraries to execute shell commands and interact with Docker

    # For demonstration purposes, let's just return the source code as a response
    return f"Source code received:\n{code}"

if __name__ == '__main__':
    app.run(debug=True)
