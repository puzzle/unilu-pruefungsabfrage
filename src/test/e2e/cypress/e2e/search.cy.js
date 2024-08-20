it('should show search page', () => {
    cy.visit("/search");

    cy.get('body').then((body) => {
        expect(body).to.contain("Such nach einer Prüfung...")
    })
})

it('should display header on search page', () => {
    cy.visit("/search");
    cy.get('header').then((header) => {
        expect(header).to.contain("Uni Luzern Prüfungsabfrage")
    })
})