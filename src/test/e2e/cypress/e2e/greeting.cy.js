it('should redirect to login page when not logged in', () => {
    cy.visit("/greeting?name=Adrian");

    cy.get('body').then((body) => {
        expect(body).to.contain("Hello Adrian")
    })
})