it('should show not found page when 404 occurs ', () => {
    cy.visit("/123");
    cy.get('body').then((body) => {
        expect(body).to.contain("Die gesuchte Seite wurde nicht gefunden");
        expect(body).to.contain("Zurück zur Startseite");
    });
});
it('should show error page when 500 occurs ', () => {
    cy.visit("/error");
    cy.get('body').then((body) => {
        expect(body).to.contain("Ein unerwarteter Fehler ist aufgetreten");
        expect(body).to.contain("Zurück zur Startseite");
    });
})
