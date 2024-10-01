Cypress.Commands.add('getByTestId', { prevSubject: 'optional' }, (subject, testId) => {
    if (subject) {
        return cy.wrap(subject).find(`[data-testId=${testId}]`);
    }
    return cy.get(`[data-testId=${testId}]`);
});
