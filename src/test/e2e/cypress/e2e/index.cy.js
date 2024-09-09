beforeEach(() => {
    cy.visit("/");
})

it('should show greeting message on index page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Willkommen bei der Prüfungsabfrage der Uni Luzern")
    })
})

it('should display header on search page', () => {
    cy.get('img').should("be.visible");
})