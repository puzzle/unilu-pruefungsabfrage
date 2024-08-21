it('should show greeting message on index page', () => {
    cy.visit("/");

    cy.get('body').then((body) => {
        expect(body).to.contain("Willkommen bei der Prüfungsabfrage der Uni Luzern")
    })
})