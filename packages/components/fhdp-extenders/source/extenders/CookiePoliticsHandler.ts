let currentLang = 'en';
let subscribed = false;
const langContent = {
    en: {
        warning: 'Attention.',
        message: 'Our website uses cookies. By continuing you agree to their use.',
        close: 'Close'
    },
    lt: {
        warning: 'Dėmesio.',
        message: 'Svetainė naudoja slapukus. Toliau naršydami Jūs sutinkate su jų naudojimu.',
        close: 'Uždaryti'
    }
}

export const addTranslationForCookiePolitics = (
    lang: string,
    element: {
        warning: string,
        message: string,
        close: string
    }
) => {
    langContent[lang] = element;
}

const removeCookieAlert = () => {
    const container = document.querySelector('.gdpr-outer');
    if (container) {
        (container as any).remove();
    }
}

const closeClick = () => {
    window.localStorage.setItem('cookieHandled', 'true');
    removeCookieAlert();
}

const renderCookieAlert = () => {
    if (window.localStorage.getItem('cookieHandled') !== 'true') {
        const container = document.createElement('div');
        container.classList.add('gdpr-outer');

        const containerInner = document.createElement('div');
        containerInner.classList.add('gdpr-inner');
        containerInner.classList.add('align-items-center');
        containerInner.classList.add('col-12');

        const row = document.createElement('div');
        row.classList.add('row');

        const text = document.createElement('div');
        text.classList.add('col-12');
        text.classList.add('col-sm-8');
        text.classList.add('col-md-9');
        text.classList.add('col-lg-10');
        text.classList.add('pb-2');
        text.innerHTML = `
        <span class="bap-dark-text gdpr-text">
            <b>${langContent[currentLang].warning}</b>
            ${langContent[currentLang].message}
        </span>`;

        row.appendChild(text);

        const buttonWrapper = document.createElement('div');
        buttonWrapper.classList.add('col-12');
        buttonWrapper.classList.add('col-sm-4');
        buttonWrapper.classList.add('col-md-3');
        buttonWrapper.classList.add('col-lg-2');
        buttonWrapper.classList.add('align-items-center');
        buttonWrapper.classList.add('d-flex');

        const button = document.createElement('button');
        button.classList.add('btn');
        button.classList.add('btn-primary');
        button.classList.add('w-100');
        button.innerText = langContent[currentLang].close;
        button.onclick = closeClick;

        buttonWrapper.appendChild(button);

        row.appendChild(buttonWrapper);

        containerInner.appendChild(row);
        container.appendChild(containerInner);
        document.body.appendChild(container);
    }
}

export const rerenderCookieAlert = (i18n: any) => {
    if (!subscribed) {
        i18n.subscribe({languageChanged: (code) => {
            currentLang = Object.keys(langContent).indexOf(code) > -1 ? code : 'en';
            removeCookieAlert();
            renderCookieAlert();
        }});
        subscribed = true;
    }
    removeCookieAlert();
    renderCookieAlert();
}