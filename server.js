cat <<EOF > server.js
const express = require('express');
const fileUpload = require('express-fileupload');
const { exec } = require('child_process');
const fs = require('fs-extra');
const path = require('path');

const app = express();
app.use(fileUpload());

app.get('/', (req, res) => {
    res.send(\`
        <div style="font-family: sans-serif; text-align: center; padding: 20px;">
            <h1>🚀 APK Builder Tool</h1>
            <h3>Upload index.html to create Android App</h3>
            <form action="/build" method="post" enctype="multipart/form-data" style="border: 2px dashed #333; padding: 20px; display: inline-block;">
                <input type="file" name="htmlFile" required accept=".html" />
                <br><br>
                <button type="submit" style="background: #28a745; color: white; padding: 10px 20px; border: none; font-size: 16px; cursor: pointer;">Build APK Now</button>
            </form>
            <p><small>Process takes 5-10 seconds...</small></p>
        </div>
    \`);
});

app.post('/build', (req, res) => {
    if (!req.files || !req.files.htmlFile) {
        return res.status(400).send('No file uploaded.');
    }

    const buildId = Date.now();
    const workDir = path.join(__dirname, 'builds', buildId.toString());
    const assetsDir = path.join(workDir, 'assets');

    // 1. Setup Folders
    fs.ensureDirSync(assetsDir);

    // 2. Copy Template APK
    if (!fs.existsSync('template.apk')) {
        return res.send('Error: template.apk missing on server!');
    }
    fs.copyFileSync('template.apk', path.join(workDir, 'base.apk'));

    // 3. Save User HTML
    const userFile = req.files.htmlFile;
    userFile.mvSync(path.join(assetsDir, 'index.html'));

    console.log(\`Building ID: \${buildId}...\`);

    // 4. Inject HTML & Sign APK
    const cmd = \`
        cd \${workDir} && \\
        zip -r base.apk assets/index.html && \\
        java -jar ../../uber-apk-signer-1.3.0.jar -a base.apk
    \`;

    exec(cmd, (error, stdout, stderr) => {
        if (error) {
            console.error(error);
            return res.status(500).send('Building Failed! Check Logs.');
        }

        const finalApk = path.join(workDir, 'base-aligned-debugSigned.apk');
        if (fs.existsSync(finalApk)) {
            res.download(finalApk, 'MyNewApp.apk');
            // Cleanup after 1 minute
            setTimeout(() => fs.remove(workDir), 60000);
        } else {
            res.send('APK Signing Failed.');
        }
    });
});

app.listen(3000, () => {
    console.log('Server is running on port 3000 🚀');
});
EOF
