const express = require('express');
const fileUpload = require('express-fileupload');
const { exec } = require('child_process');
const fs = require('fs-extra');
const path = require('path');

const app = express();
app.use(fileUpload());

app.get('/', (req, res) => {
    // यहाँ सही बैक-टिक्स (backticks) हैं
    res.send(`
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
    `);
});

app.post('/build', (req, res) => {
    if (!req.files || !req.files.htmlFile) {
        return res.status(400).send('No file uploaded.');
    }

    const buildId = Date.now();
    const workDir = path.join(__dirname, 'builds', buildId.toString());
    const assetsDir = path.join(workDir, 'assets');

    // 1. Folders बनाना
    try {
        fs.ensureDirSync(assetsDir);

        // 2. Template APK चेक करना
        if (!fs.existsSync('template.apk')) {
            return res.send('Error: template.apk missing on server! Please upload it.');
        }
        
        // APK कॉपी करना
        fs.copyFileSync('template.apk', path.join(workDir, 'base.apk'));

        // 3. यूजर की HTML सेव करना
        const userFile = req.files.htmlFile;
        userFile.mvSync(path.join(assetsDir, 'index.html'));

        console.log(`Building ID: ${buildId}...`);

        // 4. HTML इंजेक्ट करना और साइन करना
        const cmd = `cd ${workDir} && zip -r base.apk assets/index.html && java -jar ../../uber-apk-signer-1.3.0.jar -a base.apk`;

        exec(cmd, (error, stdout, stderr) => {
            if (error) {
                console.error("Error details:", error);
                return res.status(500).send('Building Failed! Check Terminal Logs.');
            }

            const finalApk = path.join(workDir, 'base-aligned-debugSigned.apk');
            
            if (fs.existsSync(finalApk)) {
                res.download(finalApk, 'MyNewApp.apk', (err) => {
                    if (!err) {
                        // 1 मिनट बाद फोल्डर डिलीट करें ताकि सर्वर न भरे
                        setTimeout(() => fs.remove(workDir), 60000);
                    }
                });
            } else {
                res.send('APK Signing Failed. Check if Java is installed.');
            }
        });

    } catch (err) {
        console.error(err);
        res.status(500).send("Server Error: " + err.message);
    }
});

app.listen(3000, () => {
    console.log('Server is running on port 3000 🚀');
});
