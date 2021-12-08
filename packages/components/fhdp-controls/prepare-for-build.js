const fs = require('fs');
const { spawn } = require("child_process");

const getVersion = (file) => {
  const package = JSON.parse(fs.readFileSync(file));
  return package.version;
}

const nodeModulesExists = fs.existsSync('./node_modules');
if (!nodeModulesExists) {
  const y = spawn("yarn", []);

  y.stdout.on("data", data => {
      console.log(`${data}`);
  });

  y.stderr.on("data", data => {
      console.log(`stderr: ${data}`);
  });

  y.on('error', (error) => {
      console.log(`error: ${error.message}`);
  });

  y.on("close", code => {
      console.log(`child process exited with code ${code}`);
  });
}

const rm = spawn("rm", [`fhdp-controls-v${getVersion('package.json')}.tgz`]);

rm.stdout.on("data", data => {
    console.log(`${data}`);
});

rm.stderr.on("data", data => {
    console.log(`stderr: ${data}`);
});

rm.on('error', (error) => {
    console.log(`error: ${error.message}`);
});

rm.on("close", code => {
    console.log(`child process exited with code ${code}`);
});
