describe('verify functionality of error page', () => {
    it('should show not found page when 404 occurs ', () => {
        cy.visit("/123");
        cy.get('body').then((body) => {
            expect(body).to.contain("Die gesuchte Seite wurde nicht gefunden");
            expect(body).to.contain("Zurück zur Startseite");
        });
    });
});

