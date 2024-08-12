it('should redirect to login page when not logged in', () => {
    cy.visit("/");

    cy.location().should((location) => {
        expect(location.href).to.eq(`${cy.config().baseUrl}/login`);
    })
})