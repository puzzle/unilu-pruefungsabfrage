beforeEach(() => {
    cy.visit("/");
    cy.get('[data-testid="cookie-consent-button"]').click();
})


it('should show greeting message on index page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Willkommen bei der Prüfungsabfrage der Uni Luzern")
    })
})

it('should display header on index page', () => {
    cy.get('header #logo').should("be.visible");
})
