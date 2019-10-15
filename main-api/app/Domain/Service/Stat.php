<?php

declare(strict_types=1);

namespace App\Domain\Service;
use App\Domain\Stats\Repository\StatsRepositoryInterface;

class Stat
{
    private $repository;

    const REGISTRATION_INCREASE_KOEF_MIN = 2;
    const REGISTRATION_INCREASE_KOEF_MAX = 3;
    const DEPOSIT_PERCENT_MIN = 50;
    const DEPOSIT_PERCENT_MAX = 70;

    public function __construct(StatsRepositoryInterface $repository)
    {
        $this->repository = $repository;
    }

    /**
     * @return bool
     */
    public function increaseRegistrationCount(): bool
    {
        $registration_count = $this->repository->getRegistrationCount();
        return $this->repository->updateRegistrationCount(
            $registration_count + mt_rand(self::REGISTRATION_INCREASE_KOEF_MIN, self::REGISTRATION_INCREASE_KOEF_MAX)
        );
    }

    /**
     * @return bool
     */
    public function increaseDepositCount(): bool
    {
        $registration_count = $this->repository->getRegistrationCount();
        $old_deposit_count = $this->repository->getDepositCount();
        $new_deposit_count = $old_deposit_count;

        while($new_deposit_count <= $old_deposit_count) {
            $new_deposit_count = $registration_count * mt_rand(self::DEPOSIT_PERCENT_MIN, self::DEPOSIT_PERCENT_MAX) / 100;
        }

        return $this->repository->updateDepositCount((int)$new_deposit_count);
    }

    public function increaseAll(): void
    {
        $this->increaseRegistrationCount();
        $this->increaseDepositCount();
    }
}