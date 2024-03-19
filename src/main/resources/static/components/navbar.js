class Navbar extends HTMLElement {
    constructor() {
        super();
    }

    connectedCallback() {
        this.innerHTML = `
            <header id="header">
                <nav class="nav">
                    <div class="nav-left">
                        <a class="brand" href="/">helmify.me</a>
                    </div>
                    <div class="nav-right">
                        <div class="tabs">
                            <a class="active" href="https://github.com/helmify/helmify">Github</a>
                            <a href="https://jenil.github.io/chota/#docs">Chota</a>
                            <a href="https://htmx.org/docs/">htmx</a>
                            <a href="/about">About</a>
                            <a href="/privacy">Privacy</a>
                        </div>
                    </div>
                </nav>
            </header>
        `

    }
}
customElements.define('navbar-component', Navbar);
