beforeEach(() => {
    cy.visit("/");
})

it('should show greeting message on index page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Willkommen bei der Prüfungsabfrage der Uni Luzern")
    })
})

it('should display header on index page', () => {
    cy.get('header').then((header) => {
        expect(header).to.contain("Uni Luzern Prüfungsabfrage")
    })
})