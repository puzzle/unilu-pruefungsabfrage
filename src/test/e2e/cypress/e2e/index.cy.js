beforeEach(() => {
    cy.visit("/");
    cy.getByTestId("cookie-consent-button").click();
})

describe('Verify content of index', () => {
    it('should show greeting message on home page', () => {
        cy.get('body').then((body) => {
            expect(body).to.contain("Willkommen bei der Prüfungsabfrage der Uni Luzern")
        })
    })

    it('should display header image on homepage', () => {
        cy.get('header #logo').should("be.visible");
    })
});
