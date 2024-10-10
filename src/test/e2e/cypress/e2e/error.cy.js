beforeEach(() => {
    cy.visit("/asdf");
})

describe('Verify functionality of error page', () => {
    it('should show not found page when 404 occurs ', () => {
        cy.get('body').then((body) => {
            expect(body).to.contain("Die gesuchte Seite wurde nicht gefunden");
            expect(body).to.contain("Zurück zur Prüfungsabfrage");
        });
    });

    it('should redirect back to main page when according button is clicked', () => {
        cy.getByTestId("back-to-main-page").click();
        cy.url().should('eq', Cypress.config().baseUrl + '/');
    })
});

