const express = require('express');
const bodyParser = require('body-parser');
const { exec } = require('child_process');
const path = require('path');

const app = express();
const port = 3000;

app.use(bodyParser.json());

app.use(express.static(path.join(__dirname, '/')));

app.post('/runcode', (req, res) => {
    const code = req.body.code;

    console.log(`Running code: ${code}`);

    const pythonFilePath = path.join(__dirname, 'temp.py');

    require('fs').writeFileSync(pythonFilePath, code);

    exec(`python3 ${pythonFilePath}`, (error, stdout, stderr) => {
        if (error) {
            res.json({ output: `Runtime Error: ${error.message}` });
        } else {
            res.json({ output: stdout });
        }

        require('fs').unlinkSync(pythonFilePath);
    });
});

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
