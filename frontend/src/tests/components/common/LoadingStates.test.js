import { render, screen } from '@testing-library/react';
import LoadingStates from '../../../components/common/LoadingStates';

describe('LoadingStates Component', () => {
  test('renders default loading state', () => {
    render(<LoadingStates />);
    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  test('renders skeleton loading state', () => {
    render(<LoadingStates type="skeleton" />);
    const skeletonElements = screen.getAllByTestId('skeleton-item');
    expect(skeletonElements).toHaveLength(3);
  });

  test('renders spinner loading state', () => {
    render(<LoadingStates type="spinner" />);
    expect(screen.getByTestId('spinner')).toBeInTheDocument();
  });

  test('renders pulse loading state', () => {
    render(<LoadingStates type="pulse" />);
    expect(screen.getByTestId('pulse')).toBeInTheDocument();
  });
});
