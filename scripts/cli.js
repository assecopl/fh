const runtime = require('./libs/Runtime');
const utils = require('./libs/Utils');

class CLI {
  constructor() {
    const params = utils.parseParams();
    if (this.validate(params.processName)) {
      runtime._run(params.processName, params);
    } else {
      console.log(`
        Process name is invalid. 
        valid process are:
        ${Object.getOwnPropertyNames(Object.getPrototypeOf(runtime)).filter(fn => this.validate(fn)).join(', ')}
        You can allways use --help param to additional information.
      `)
    }
  }
  
  validate(pName) {
    const match = pName.match(/[a-zA-Z0-9]+/);
    return match[0] === match.input && pName !== 'constructor';
  }
}

new CLI();