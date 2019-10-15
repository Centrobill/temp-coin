<?php

declare(strict_types=1);

namespace App\Domain\Stats\Repository;

/**
 * Interface WalletRepositoryInterface
 * @package App\Domain\Stats\Repository
 */
interface WalletRepositoryInterface
{
    public function getWalletStatus(string $currency, array $attributes): array;
    public function getWalletsStatus(array $attributes): array;
    public function withdraw($currency, $address, $amount): array;
}