it('should show greeting according to query param on greeting page', () => {
    cy.visit("/greeting?name=Adrian");

    cy.get('body').then((body) => {
        expect(body).to.contain("Hello Adrian")
    })
})